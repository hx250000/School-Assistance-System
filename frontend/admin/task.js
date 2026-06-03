console.log("task page loaded");

const BASE = API.task;

function getStatusClass(status){

    if(!status){
        return "";
    }

    let s = status.toLowerCase();

    if(
        s.includes("finish") ||
        s.includes("completed")
    ){
        return "status-finished";
    }

    if(
        s.includes("cancel")
    ){
        return "status-cancel";
    }

    return "status-pending";
}

async function loadAllTasks(){

    await loadStatus(
        "OPEN",
        "openTable"
    );

    await loadStatus(
        "IN_PROGRESS",
        "progressTable"
    );

    await loadStatus(
        "FINISHED",
        "finishedTable"
    );

    await loadStatus(
        "CANCELLED",
        "cancelledTable"
    );
}

async function loadStatus(
    status,
    tableId
){

    try{

        let res =
            await fetch(
                BASE +
                "/admin/list?page=0&size=100&status=" +
                status
            );

        let data =
            await res.json();

        renderTable(
            tableId,
            data.data
        );

    }
    catch(e){

        console.error(e);

    }
}

function renderTable(
    tableId,
    tasks
){

    let html = "";

    if(
        !tasks ||
        tasks.length === 0
    ){

        html = `
        <tr>
            <td colspan="5">
                暂无数据
            </td>
        </tr>
        `;

        document
            .getElementById(tableId)
            .innerHTML = html;

        return;
    }

    tasks.forEach(t => {

        html += `
        <tr>

            <td>${t.taskId}</td>

            <td>${t.title}</td>

            <td>${t.rewardPoints}</td>

            <td class="${getStatusClass(t.status)}">

                ${t.status}

            </td>

            <td>

                <button
                    class="finish-btn"
                    onclick="finishTask(${t.taskId})">

                    完成

                </button>

                <button
                    class="cancel-btn"
                    onclick="cancelTask(${t.taskId})">

                    取消

                </button>

            </td>

        </tr>
        `;
    });

    document
        .getElementById(tableId)
        .innerHTML = html;
}

async function finishTask(id){

    try{

        let res =
            await fetch(
                BASE +
                "/" +
                id +
                "/finish",
                {
                    method:"POST"
                }
            );

        if(res.ok){

            alert("任务已完成");

            loadAllTasks();
        }

    }
    catch(e){

        console.error(e);

        alert("操作失败");
    }
}

async function cancelTask(id){

    try{

        let res =
            await fetch(
                BASE +
                "/" +
                id +
                "/cancel",
                {
                    method:"POST"
                }
            );

        if(res.ok){

            alert("任务已取消");

            loadAllTasks();
        }

    }
    catch(e){

        console.error(e);

        alert("操作失败");
    }
}

async function searchTask(){

    try{

        let keyword =
            document
            .getElementById("keyword")
            .value;

        if(!keyword){

            document
                .getElementById("searchTable")
                .innerHTML = "";

            return;
        }

        let res =
            await fetch(
                BASE +
                "/search?keyword=" +
                encodeURIComponent(keyword)
            );

        let data =
            await res.json();

        renderTable(
            "searchTable",
            data.data
        );

    }
    catch(e){

        console.error(e);

        alert("搜索失败");
    }
}

loadAllTasks();