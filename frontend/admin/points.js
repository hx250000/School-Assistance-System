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

            console.log(result);

            alert(result.message);

            return;
        }

        const data = result.data;

        console.log(data);

        document
            .getElementById("currentPoints")
            .innerText =
            data.currentPoints || 0;

        renderTable(
            userId,
            data.pointsHistoryList || []
        );

    }
    catch(e){

        console.error(e);

        alert("查询失败");
    }
}

function renderTable(userId,logs){

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

            <td>${userId}</td>

            <td>${log.changeAmount>0 ? '+' + log.changeAmount : log.changeAmount}</td>

            <td>${log.title || ""}</td>

            <td>${log.description || ""}</td>

            <td>${formatTime(log.time)}</td>

        </tr>
        `;
    });

    document
        .getElementById("pointsTable")
        .innerHTML = html;
}