layui.use(["table", "layer", "form"], ()=>{
    renderRoomTbl();
});

function renderRoomTbl(){
    let table = layui.table;

    table.render({
        elem: "#roomTbl",
        url: "/chatroom/room/list",
        parseData: basePageMapper,
        cols: roomColsMapper,
        toolbar: "#roomTblToolbar"
    });

    table.on("toolbar(roomTbl)", obj=>{
        let event = obj.event, data = obj.data;
        console.log(obj);
        if (event === "addRoom"){
            openAddRoomForm();
        }
    });

    table.on("tool(roomTbl)", obj=>{
        let event = obj.event, data = obj.data, $ = layui.jquery;
        console.log(obj);
        if (event === "deleteRoom"){
            postAndAlertMessage("/chatroom/room/delete/"+data.id);
            renderRoomTbl();
        }
    });
}

function openAddRoomForm(){
    layui.layer.open({
        title: "添加房间",
        type: 1,
        content: layui.jquery("#addRoomFormScript")[0].innerHTML,
        btn: ["提交","取消"],
        yes: (index, layero) => {
            let obj = getObjFromForm(layui.jquery, "addRoomForm");
            postAndAlertMessage("/chatroom/room/add", obj, res =>{
                responseProcessor.alertMessage(res);
                renderRoomTbl();
            });
            layui.layer.close(layui.layer.index);
        }
    });
}

let roomColsMapper = [[
    {
        field: "id",
        title: "id",
        unresize: true
    },{
        field: "description",
        title: "题目描述",
        unresize: true

    },{
        title: "操作",
        fixed: "right",
        width: 150,
        align:"center",
        toolbar: '#roomTblOps'
    }
]];