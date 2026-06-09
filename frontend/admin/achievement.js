const BASE = API.achievement;

async function loadAchievements(){

    let res =
        await fetch(
            BASE + "/admin/list/achievements"
        );

    let data =
        await res.json();

    let html = "";

    data.data.forEach(a=>{

        html += `
        <tr>

            <td>${a.id}</td>
            <td>${a.title}</td>
            <td>${a.description}</td>
            <td>${a.type}</td>
            <td>${a.conditionValue}</td>
            <td>${a.rewardPoints}</td>
            <td>${a.iconUrl || ""}</td>
            <td>${a.isActive ? "是" : "否"}</td>

        </tr>
        `;
    });

    document
        .getElementById("achievementTable")
        .innerHTML = html;
}

async function addAchievement(){

    await fetch(
        BASE + "/admin/achievement",
        {
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify({
                title:document.getElementById("title").value,
                description:document.getElementById("description").value,
                type:document.getElementById("type").value,
                conditionValue:Number(document.getElementById("conditionValue").value),
                rewardPoints:Number(document.getElementById("rewardPoints").value),
                iconUrl:document.getElementById("iconUrl").value,
                isActive:true
            })
        }
    );

    alert("新增成功");

    loadAchievements();
}

async function loadUserAchievements(){

    let userId =
        document.getElementById("userId").value;

    let res =
        await fetch(
            BASE +
            "/admin/list/userachievements?userId=" +
            userId
        );

    let data =
        await res.json();

    let html = "";

    data.data.userAchievements.forEach(item=>{

        html += `
        <tr>

            <td>${item.achievement.title}</td>

            <td>${item.currentProgress}</td>

            <td>
                ${item.unlocked ? "已解锁" : "未解锁"}
            </td>

            <td>
                ${item.unlockedAt || ""}
            </td>

        </tr>
        `;
    });

    document
        .getElementById("userAchievementTable")
        .innerHTML = html;
}

async function initAchievements(){

    await fetch(
        BASE + "/admin/init",
        {
            method:"POST"
        }
    );

    alert("初始化完成");
}

async function recalculateAchievements(){

    await fetch(
        BASE + "/admin/recalculate",
        {
            method:"POST"
        }
    );

    alert("刷新完成");
}

loadAchievements();