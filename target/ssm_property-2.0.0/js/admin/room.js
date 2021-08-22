function showPicture(cellvalue){
    /*../image/bg3.jpg*/
    return "<img src='../../" + cellvalue + "' alt='" + cellvalue + "' height='100' width='100'/>";
}

$(function () {
    $("#jqGrid").jqGrid({
        url: '../room/list',
        datatype: "json",
        colModel: [
            {
                label: 'id',
                name: 'id',
                index: 'id',
                width: 50,
                key: true,
                hidden: true
            }, {
                label: '所属楼栋',
                name: 'houseEntity.name',
                index: 'house',
                width: 80
            },
            {
                label: '房号',
                name: 'name',
                index: 'name',
                width: 80,
                formatter: function (v, a, r) {
                    return "<a onclick='vm.info(\"" + r.id + "\")'>" + v + " </a>"
                }
            },

            {
                label: '面积',
                name: 'size',
                index: 'size',
                width: 80
            },
            /*{
                //列表中显示图片
                label: '图片', name: 'photo',index:'photo', width: 80, formatter: function (cellvalue) {
                    return '<img src="" style="width: 40px;height: 50px" />';
                }
            }*/
            //这点击文件上传的cover传给后台的是一个纯路径，而不是MultipartFile类型，所以文件上传还是需要upload插件
           /* {label: '图片', name: 'photo',index:'photo', width: 80, editable : true, edittype:"file",
                formatter:function (value, options, row) {
                    return "<img style='width:70px;height:35px' src='../file/show?path="+vm.room.photo+"'>";
                }
            },
*/
            {label: '图片',name:'photo',index:'photo', align:'center',editable: true,formatter:showPicture,edittype:'file',
                editoptions:{enctype:"multipart/form-data"}}
            /*{
                label : "图片",
                name :"photo",
                index:"photo",
                width : 100,
                formatter : function(value, options, row) {// value:当前对象 row：当前行
                    if (value != null) {
                        return "<img src='" + value + "'  width='50px' />";
                    } else {
                        return value;
                    }
                }
            }*/
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    new AjaxUpload('#upload', {
        action: '../file/upload',
        name: 'file',
        autoSubmit:true,
        responseType:"json",
        onSubmit:function(file, extension){
            // if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))){
            //     alert('只支持jpg、png、gif格式的图片！');
            //     return false;
            // }
        },
        onComplete : function(file, r){
            if(r.code == 0){
                vm.room.photo=r.url;
                console.log(r);
                alert(r.url);
            }else{
                alert(r.msg);
            }
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        q: {
            name: ''
        },
        showAdd: false,
        showInfo: false,
        showList: true,
        title: null,
        room: {},
        list: []
    },
    created: function () {
        $.getJSON("../house/list2", function (r) {
            vm.list = r.list;
            console.log(r.list);
        })
    },
    methods: {
        query: function () {
            vm.reload();
        }
        ,
        add: function () {
            vm.showAdd = true;
            vm.showList = false;
            vm.title = "新增";
            vm.room = {};
        }
        ,
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }

            vm.showAdd = true;
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(id)
        }
        ,
        saveOrUpdate: function (event) {
            var url = vm.room.id == null ? "../room/save" : "../room/update";
            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(vm.room),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        }
        ,
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: "../room/delete",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        }
        ,
        getInfo: function (id) {
            $.get("../room/info/" + id, function (r) {
                vm.room = r.room;
            });
        }
        ,
        info: function (id) {
            if (isNaN(id)) {
                id
                    = getSelectedRow();
                if (id == null
                ) {
                    return;
                }
            }
            vm.showAdd = false;
            vm.showList = false;
            vm.showInfo = true;
            vm.title = "详情";
            vm.getInfo(id)
        }
        ,
        reload: function (event) {
            debugger;
            vm.showList = true;
            vm.showInfo = false;
            vm.showAdd = false;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: vm.q,
                page: page
            }).trigger("reloadGrid");
        }
    }
});