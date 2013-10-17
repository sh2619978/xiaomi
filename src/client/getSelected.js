//购票主方法

//参数格式：#号分隔
function getSelected(selectStr) {
    var selectStr_arr = selectStr.split("#");
    var station_train_code=selectStr_arr[0];
    var lishi=selectStr_arr[1];
    var starttime=selectStr_arr[2];
    var trainno=selectStr_arr[3];
    var from_station_telecode=selectStr_arr[4];
    var to_station_telecode=selectStr_arr[5];
    var arrive_time=selectStr_arr[6];
    var from_station_name=selectStr_arr[7];
    var to_station_name=selectStr_arr[8];
    var ypInfoDetail=selectStr_arr[9];
    var flag = true;
    if (checkBeyondMixTicketNum()) {
        flag = false;
        return;
    }
    // 该方法在各个页面中分别书写，因为根据页面的不同行为不同，相当于重写
    if (flag) {
        submitRequest(station_train_code,lishi,starttime,trainno,from_station_telecode,to_station_telecode,arrive_time,from_station_name,to_station_name,ypInfoDetail);
    }
}


//发送购票请求开始
function submitRequest(station_train_code,lishi,starttime,trainno,from_station_telecode,to_station_telecode,arrive_time,from_station_name,to_station_name,ypInfoDetail) {
$('#station_train_code').val(station_train_code);
$('#lishi').val(lishi);
$('#train_start_time').val(starttime);
$('#trainno').val(trainno);
$('#from_station_telecode').val(from_station_telecode);
$('#to_station_telecode').val(to_station_telecode);
$('#arrive_time').val(arrive_time);
$('#from_station_name').val(from_station_name);
$('#to_station_name').val(to_station_name);
$('#ypInfoDetail').val(ypInfoDetail);
$('#orderForm').attr("action",
ctx+"/order/querySingleAction.do?method=submutOrderRequest");
$('#orderForm').submit();
}
// 发送购票请求结束 



