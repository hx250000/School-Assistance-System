const API_ENV = "local"; // "local" | "railway"

const ENV = {
  local:   "http://localhost:8080",
  railway: "https://school-assistance-system.up.railway.app"
};

const BASE_URL = ENV[API_ENV] || ENV.railway;

// 各页面共用的 API 前缀
const API = {
  user: BASE_URL + "/api/user",
  task: BASE_URL + "/api/task",
  shop: BASE_URL + "/api/shop",
  achievement: BASE_URL + "/api/achievements",
  points: BASE_URL + "/api/points",
  file: BASE_URL + "/"   // 头像等静态资源
};