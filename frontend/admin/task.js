console.log("task page loaded");

const BASE = API.task;

function getStatusClass(status){

    if(!status){
        return "";
    }

    let s = status.toLowerCase();

    if(s.includes("finish")){
        return "status-finished";
    }

    if(s.includes("cancel")){
        return "status-cancel";
    }

    return "status-pending";
}

async function loadAllTasks(){

    await loadStatus("OPEN", "openTable");
    await loadStatus("IN_PROGRESS", "progressTable");
    await loadStatus("FINISHED", "finishedTable");
    await loadStatus("CANCELLED", "cancelledTable");
}

async function loadStatus(status, tableId){

    try{

        let res = await fetch(
            BASE + "/admin/list?page=0&size=100&status=" + status
        );

        let data = await res.json();

        renderTable(tableId, data.data);

    }catch(e){
        console.error(e);
    }
}

function renderTable(tableId, tasks){

    let html = "";

    if(!tasks || tasks.length === 0){

        html = `
        <tr>
            <td colspan="4">暂无数据</td>
        </tr>
        `;

        document.getElementById(tableId).innerHTML = html;
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

        </tr>
        `;
    });

    document.getElementById(tableId).innerHTML = html;
}

async function searchTask(){

    try{

        let keyword = document.getElementById("keyword").value;

        if(!keyword){
            document.getElementById("searchTable").innerHTML = "";
            return;
        }

        let res = await fetch(
            BASE + "/search?keyword=" + encodeURIComponent(keyword)
        );

        let data = await res.json();

        renderTable("searchTable", data.data);

    }catch(e){
        console.error(e);
        alert("搜索失败");
    }
}

loadAllTasks();