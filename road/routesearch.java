//修改图片即可

public class RouteSearchActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener, View.OnClickListener {

    private MapView mMapView;
    private AMap aMap;
    private TextView content;

    private static final String TAG = "RouteSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_search);

        findViewById(R.id.route_bus).setOnClickListener(this);
        findViewById(R.id.route_driver).setOnClickListener(this);
        findViewById(R.id.route_walk).setOnClickListener(this);
        findViewById(R.id.route_ride).setOnClickListener(this);
        content = (TextView) this.findViewById(R.id.route_content);
        mMapView = (MapView) this.findViewById(R.id.map);

        // 此方法须重写，虚拟机需要在很多情况下保存地图绘制的当前状态
        mMapView.onCreate(savedInstanceState);
        //默认公交
        setRouteCarListener(1);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route_bus:
                setRouteCarListener(1);
                break;
            case R.id.route_driver:
                setRouteCarListener(0);
                break;
            case R.id.route_walk:
                setRouteCarListener(2);
                break;
            case R.id.route_ride:
                setRouteCarListener(3);
                break;
        }
    }

    /**
     * 自带dialog进度
     */
    private ProgressDialog dialog;

    /**
     * 规划线路
     * type 0 驾车 1 公交 2 步行 3 骑行
     *
     * @param type
     */
    private void setRouteCarListener(int type) {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //清除防止重复显示
        aMap.clear();
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setMessage("正在规划路线，请稍后...");
        dialog.show();

        RouteSearch routeSearch = new RouteSearch(this);
        //模拟起始点与目的经纬度（如：深圳市）
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(22.5587, 113.8727),
                new LatLonPoint(22.5587, 113.8950));
        //驾车：第一个参数表示fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式(支持20种模式  -在PathPlanningStrategy类中定义)
        //第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路
        //模式链接：http://lbs.amap.com/api/android-navi-sdk/guide/route-plan/drive-route-plan
        if (type == 0) {
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, PathPlanningStrategy.DRIVING_DEFAULT, null, null, "");
            routeSearch.calculateDriveRouteAsyn(query);
        } else if (type == 1) {
            //公交：fromAndTo包含路径规划的起点和终点，RouteSearch.BusLeaseWalk表示公交查询模式
            //第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算,1表示计算
            RouteSearch.BusRouteQuery query1 = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusLeaseWalk, "0755", 1);//深圳区号
            routeSearch.calculateBusRouteAsyn(query1);
        } else if (type == 2) {
            //步行：SDK提供两种模式：RouteSearch.WALK_DEFAULT 和 RouteSearch.WALK_MULTI_PATH（注意：过时）
            RouteSearch.WalkRouteQuery query2 = new RouteSearch.WalkRouteQuery(fromAndTo);
            routeSearch.calculateWalkRouteAsyn(query2);
        } else if (type == 3) {
            //骑行：（默认推荐路线及最快路线综合模式，可以接二参同上）
            RouteSearch.RideRouteQuery query3 = new RouteSearch.RideRouteQuery(fromAndTo);
            routeSearch.calculateRideRouteAsyn(query3);
        }
        routeSearch.setRouteSearchListener(this);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        dialog.dismiss();
        if (i == 1000) {
            BusPath busPath = busRouteResult.getPaths().get(0);
            setBusRoute(busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
            float distance = busPath.getDistance() / 1000;
            long duration = busPath.getDuration() / 60;
            //需步行距离
            float walkdistance = busPath.getWalkDistance() / 1000;
            //行车的距离
            float busdistance = busPath.getBusDistance() / 1000;
            //成本、费用（其中walkdistance+busdistance=distance 行车+步行=总距离）
            float cost = busPath.getCost();
            content.setText("\n距离/公里：" + distance + "\n时间/分：" + duration + "\n步行距离/公里：" + walkdistance
                    + "\n行车距离/公里：" + busdistance + "\n成本、费用：" + cost);
        } else {
            Log.e(TAG, "onBusRouteSearched: 路线规划失败");
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        dialog.dismiss();
        if (i == 1000) {
            DrivePath drivePath = driveRouteResult.getPaths().get(0);
            setDrivingRoute(drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
            //策略
            String strategy = drivePath.getStrategy();
            //总的交通信号灯数
            int clights = drivePath.getTotalTrafficlights();
            //距离 米：/1000转公里 1公里=1km
            float distance = drivePath.getDistance() / 1000;
            //时间 秒：、60转分
            long duration = drivePath.getDuration() / 60;
            content.setText("策略：" + strategy + "\n总的交通信号灯数/个：" + clights +
                    "\n距离/公里：" + distance + "\n时间/分：" + duration);
        } else {
            Log.e(TAG, "onDriveRouteSearched: 路线规划失败");
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        dialog.dismiss();
        if (i == 1000) {
            WalkPath walkPath = walkRouteResult.getPaths().get(0);
            setWalkRoute(walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
            float distance = walkPath.getDistance() / 1000;
            long duration = walkPath.getDuration() / 60;
            content.setText("\n距离/公里：" + distance + "\n时间/分：" + duration);
        } else {
            Log.e(TAG, "onWalkRouteSearched: 路线规划失败");
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        dialog.dismiss();
        if (i == 1000) {
            RidePath ridePath = rideRouteResult.getPaths().get(0);
            setRideRoute(ridePath, rideRouteResult.getStartPos(), rideRouteResult.getTargetPos());
            float distance = ridePath.getDistance() / 1000;
            long duration = ridePath.getDuration() / 60;
            content.setText("\n距离/公里：" + distance + "\n时间/分：" + duration);
        } else {
            Log.e(TAG, "onRideRouteSearched: 路线规划失败");
        }
    }

    /**
     * 驾车线路
     */
    private void setDrivingRoute(DrivePath drivePath, LatLonPoint start, LatLonPoint end) {
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, start, end);
        drivingRouteOverlay.setNodeIconVisibility(true);//设置节点（转弯）marker是否显示
        drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
        drivingRouteOverlay.removeFromMap();//去掉DriveLineOverlay上的线段和标记。
        drivingRouteOverlay.addToMap(); //添加驾车路线添加到地图上显示。
        drivingRouteOverlay.zoomToSpan();//移动镜头到当前的视角。
        drivingRouteOverlay.setRouteWidth(1);//设置路线的宽度
    }

    /**
     * 公交规划线路
     */
    private void setBusRoute(BusPath busPath, LatLonPoint start, LatLonPoint end) {
        BusRouteOverlay busRouteOverlay = new BusRouteOverlay(this, aMap, busPath, start, end);
        busRouteOverlay.removeFromMap();//去掉DriveLineOverlay上的线段和标记。
        busRouteOverlay.addToMap(); //添加驾车路线添加到地图上显示。
        busRouteOverlay.zoomToSpan();//移动镜头到当前的视角。
        busRouteOverlay.setNodeIconVisibility(true);//是否显示路段节点图标

    }

    /**
     * 步行规划线路
     */
    private void setWalkRoute(WalkPath walkPath, LatLonPoint start, LatLonPoint end) {
        WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, start, end);
        walkRouteOverlay.removeFromMap();
        walkRouteOverlay.addToMap();
        walkRouteOverlay.zoomToSpan();
    }

    /**
     * 骑行规划线路
     */
    private void setRideRoute(RidePath ridePath, LatLonPoint start, LatLonPoint end) {
        RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(this, aMap, ridePath, start, end);
        rideRouteOverlay.removeFromMap();
        rideRouteOverlay.addToMap();
        rideRouteOverlay.zoomToSpan();
    }
}