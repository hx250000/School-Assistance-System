const BASE = API.points;

function formatTime(time){

    if(!time){
        return "";
    }

    return time.replace("T"," ");
}

async function searchUserPoints(){

    const userId =
        document
        .getElementById("userId")
        .value
        .trim();

    if(!userId){

        alert("请输入用户ID");

        return;
    }

    try{

        const res =
            await fetch(
                BASE +
                "/admin/history?userId=" +
                userId
            );

        const result =
            await res.json();

        if(result.code !== 200){

            alert(result.message);

            return;
        }

        const data = result.data;

        document
            .getElementById("currentPoints")
            .innerText =
            data.currentPoints || 0;

        renderTable(
            data.logs || []
        );

    }
    catch(e){

        console.error(e);

        alert("查询失败");
    }
}

function renderTable(logs){

    let html = "";

    if(logs.length === 0){

        html = `
        <tr>
            <td colspan="5">
                暂无积分记录
            </td>
        </tr>
        `;

        document
            .getElementById("pointsTable")
            .innerHTML = html;

        return;
    }

    logs.forEach(log=>{

        html += `
        <tr>

            <td>${log.id}</td>

            <td>${log.changeAmount}</td>

            <td>${log.title || ""}</td>

            <td>${log.description || ""}</td>

            <td>${formatTime(log.createdAt)}</td>

        </tr>
        `;
    });

    document
        .getElementById("pointsTable")
        .innerHTML = html;
}