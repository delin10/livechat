let userInfo = {
    id: "1",
    username: "lidelin",
    nickname: "神秘的范儿",
    is_verified: true,
    secret_key: "1bcdefghijklmn",
    head_img: "http://i1.sinaimg.cn/ent/d/2008-06-04/U105P28T3D2048907F326DT20080604225106.jpg"
};
layui.use(["form", "layer", "upload", "laytpl", "upload", "table"], function () {
    let $ = layui.jquery, form = layui.form;
    $.get("/livechat/user/info/get", res => {
        userInfo = res.data;
        initHeadImg();
        initAdditionalInfoComponent();
    });
    form.on("submit(submit-basic-info-btn)", data => {
        let submitObj = data.field;
        postAndAlertMessage("/livechat/user/info/edit", {nickname: submitObj.nickname || ''}, res => {
            if (res.code === 0) {
                alert("修改成功");
            } else {
                alert("修改失败");
            }
        });
        return false;
    });


    // 文件上传组件
    renderUploadComponent();
});

function refresh() {
    window.location.reload()
}

function initAdditionalInfoComponent() {
    let $ = layui.jquery, laytpl = layui.laytpl, form = layui.form, table = layui.table;
    $("#additional-container").append(
        laytpl(additionalTpl).render(userInfo));
    form.val("basic-form", userInfo);
    $("#verifiedFormBtn").on("click", openVerifiedForm);
    $("#modifyPwdFormBtn").on("click", openModifyPwdForm);
    $("#create-room-btn").on("click", openAddRoomForm);
    table.render({
        elem: "#roomTbl",
        data: userInfo.rooms,
        cols: roomColsMapper,
        toolbar: "#roomTblToolbar"
    });

    table.on("tool(roomTbl)", obj => {
        let event = obj.event, data = obj.data;
        console.log(obj);
        if (event === "editRoom") {
            openEditRoomForm(data);
        }
    });
}

function openAddRoomForm() {
    layui.layer.open({
        title: "创建房间",
        type: 1,
        content: addRoomFormHtml,
        btn: ["提交", "取消"],
        success: (index, layero) => {
            renderTagSelect();
            renderCoverUploadComponent("addRoomForm");
        },
        yes: (index, layero) => {
            let obj = getObjFromForm(layui.jquery, "addRoomForm");
            obj.tags = selectEl.getValue("value");
            postAndAlertMessage("/livechat/room/create", obj, res => {
                if (res.code === 0) {
                    alert("创建成功");
                    refresh();
                } else {
                    alert("创建失败");
                }
            });
            layui.layer.close(layui.layer.index);
        }
    });
}

function openEditRoomForm(room) {
    layui.layer.open({
        title: "编辑房间",
        type: 1,
        content: editRoomFormHtml,
        btn: ["提交", "取消"],
        success: (index, layero) => {
            renderTagSelect(room.tags.map(e => e.id));
            renderCoverUploadComponent("editRoomForm");
            layui.form.val("editRoomForm", room);
            initCoverImg(room.cover);
            layui.form.render();
        },
        yes: (index, layero) => {
            let obj = getObjFromForm(layui.jquery, "editRoomForm");
            obj.id = room.id;
            obj.tags = selectEl.getValue("value");
            delete obj.secret_key;
            postAndAlertMessage("/livechat/room/edit", obj, res => {
                if (res.code === 0) {
                    alert("修改成功");
                    refresh();
                } else {
                    alert("修改失败");
                }
            });
            layui.layer.close(layui.layer.index);
        }
    });
}

function initHeadImg() {
    let headElem = layui.jquery("#head-span");
    headElem.empty();
    headElem.append(`<a href="javascript:"><img src="${userInfo.head_img}" class="layui-nav-img"/></a>`);
}

function initCoverImg(cover) {
    let coverElem = layui.jquery("#cover-span");
    coverElem.empty();
    coverElem.append(`<a href="javascript:"><img src="${cover}" class="layui-nav-img"/></a>`);
}

function openVerifiedForm() {
    console.log("Start to call openVerifiedForm");
    let formId = "verifiedForm";
    let formTitle = "认证信息";
    let formUrl = "/livechat/user/info/actual/verify";
    layui.layer.open({
        title: formTitle,
        type: 1,
        content: layui.jquery(`#${formId}Script`)[0].innerHTML,
        btn: ["提交", "取消"],
        yes: (index, layero) => {
            let obj = getObjFromForm(layui.jquery, `${formId}`);
            postAndAlertMessage(formUrl, obj, res => {
                responseProcessor.alertMessage(res);
            });
            layui.layer.close(layui.layer.index);
        }
    });
}

function openModifyPwdForm() {
    console.log("Start to call openModifyPwdForm")
    let formId = "modifyPwdForm";
    let formTitle = "修改密码";
    let formUrl = "/livechat/user/info/pwd/change";
    layui.layer.open({
        title: formTitle,
        type: 1,
        content: layui.jquery(`#${formId}Script`)[0].innerHTML,
        btn: ["提交", "取消"],
        yes: (index, layero) => {
            let obj = getObjFromForm(layui.jquery, `${formId}`);
            if (obj.confirm_pwd !== obj.new_pwd) {
                alert("两次输入密码不一致");
                return;
            }
            delete obj.confirm_pwd;
            postAndAlertMessage(formUrl, obj, res => {
                responseProcessor.alertMessage(res);
            });
            layui.layer.close(layui.layer.index);
        }
    });
}

function renderCoverUploadComponent(formFilter) {
    let upload = layui.upload, form = layui.form;
    upload.render({
        elem: "#upload-cover-img",
        url: "/livechat/file/upload",
        field: "file",
        done: res => {
            let preparedImg = host + `/livechat/file/download?bucket=${res.data.bucket}`;
            if (res.code === 0) {
                initCoverImg(preparedImg);
                form.val(formFilter, {cover: preparedImg});
                alert("上传成功");
                return;
            }
            alert("上传失败");
        }
    });
}

function renderUploadComponent() {
    let upload = layui.upload;
    upload.render({
        elem: "#upload-head-img",
        url: "/livechat/file/upload",
        field: "file",
        done: res => {
            let preparedImg = host + `/livechat/file/download?bucket=${res.data.bucket}`;
            if (res.code === 0) {
                postAndAlertMessage("/livechat/user/info/head/update", {head_img: preparedImg}, updateRes => {
                    if (updateRes.code === 0) {
                        userInfo.head_img = preparedImg;
                        initHeadImg();
                        alert("上传成功");
                    }
                });
                return;
            }
            alert("上传失败");
        }
    });
}
let selectEl;
function renderTagSelect(selectIds) {
    let $ = layui.jquery;
    $.get("/livechat/tag/list/all", res => {
        if (res.code === 0) {
            let selects = res.data.map(e => {
                let obj = {"name": e.word, "value": e.id};
                if (selectIds && selectIds.indexOf(e.id) >= 0){
                    obj.selected = true;
                }
                return obj;
            });
            selectEl = xmSelect.render({
                el: "#tag-selects",
                data:selects
            });
        } else {
            alert("系统错误");
        }
    })
}

let additionalTpl =
    "{{# if(!d.is_verified) { }}\n" +
    "        <div class=\"layui-form-item\">\n" +
    "            <label class=\"layui-form-label\">成为主播</label>\n" +
    "            <div class=\"layui-input-block\">\n" +
    "                <button id=\"verifiedFormBtn\" type=\"button\" class=\"layui-btn\">认证信息</button>\n" +
    "            </div>\n" +
    "        </div>\n" +
    "        {{# } else { }}\n" +
    "        <div class=\"layui-form-item\">\n" +
    "            <label class=\"layui-form-label\">房间列表</label>" +
    "                   {{# if(d.rooms.length === 0 ) { }}" +
    "                       <button type=\"button\" class=\"layui-btn\" id=\"create-room-btn\">\n" +
    "                          创建房间\n" +
    "                        </button>" +
    "                   {{# } }}" +
    "            <div class=\"layui-input-block\">\n" +
    "                <table id=\"roomTbl\" lay-filter=\"roomTbl\"></table>" +
    "            </div>\n" +
    "        </div>\n" +
    "        {{# } }}";

let editRoomFormHtml =
    `<form id="editRoomForm" class="layui-form" lay-filter="editRoomForm" style="margin-top: 20px">
            <div class="layui-form-item">
                <label class="layui-form-label">房间标题</label>
                <div class="layui-input-inline">
                    <input  type="text" name="title" required placeholder="请输入房间标题" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">标签</label>
                <div id="tag-selects" class="layui-input-inline">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">房间描述</label>
                <div class="layui-input-inline">
                    <input  type="text" name="description" required placeholder="请输入房间描述" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">直播密钥</label>
                <div class="layui-input-inline">
                    <input  type="text" name="secret_key" disabled autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">直播封面</label>
                <div class="layui-input-block">
                    <span id="cover-span"></span>
                    <input  type="text" name="cover" style="display:none;" class="layui-input">
                    <button type="button" class="layui-btn" id="upload-cover-img">
                        <i class="layui-icon">&#xe67c;</i>上传图片
                    </button>
                </div>
            </div>
        </form>`;

let addRoomFormHtml =
    `<form id="addRoomForm" class="layui-form" lay-filter="addRoomForm" style="margin-top: 20px">
            <div class="layui-form-item">
                <label class="layui-form-label">房间标题</label>
                <div class="layui-input-inline">
                    <input  type="text" name="title" required placeholder="请输入房间标题" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">标签</label>
                <div id="tag-selects" class="layui-input-inline">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">房间描述</label>
                <div class="layui-input-inline">
                    <input  type="text" name="description" required placeholder="请输入房间描述" autocomplete="off" class="layui-input">
                </div>
            </div>
            <input  type="text" name="cover" style="display:none;" class="layui-input">
            <div class="layui-form-item">
                <label class="layui-form-label">直播封面</label>
                <div class="layui-input-block">
                    <span id="cover-span"></span>
                    <button type="button" class="layui-btn" id="upload-cover-img">
                        <i class="layui-icon">&#xe67c;</i>上传图片
                    </button>
                </div>
            </div>
        </form>`;
let roomColsMapper = [[
    {
        field: "id",
        title: "id",
        unresize: true
    }, {
        field: "description",
        title: "描述",
        unresize: true

    }, {
        field: "title",
        title: "标题",
        unresize: true

    }, {
        field: "tags",
        title: "标签",
        unresize: true,
        templet: d => d.tags.map(e => e.word).join(",")
    }, {
        field: "cover",
        title: "封面",
        unresize: true,
        templet: d => `<img src="${d.cover}"/>`
    }, {
        field: "secret_key",
        title: "密钥",
        unresize: true,
    }, {
        title: "操作",
        fixed: "right",
        width: 150,
        align: "center",
        toolbar: '#roomTblOps'
    }
]];