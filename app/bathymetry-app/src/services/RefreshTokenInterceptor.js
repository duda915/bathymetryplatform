import axios from "axios";
import { restBasicToken } from "./ServiceMetaData";
import Cookies from "universal-cookie";

export function registerInterceptor() {
  const cookies = new Cookies();

  axios.interceptors.response.use(null, error => {
    if (error.config && error.response && error.response.status === 401) {
      const refreshTokenConfig = {
        headers: {
          Authorization: restBasicToken
        }
      };

      if (cookies.get("refresh_token") === "signedout") {
        return Promise.reject(error);
      }

      if (cookies.get("refresh_token") === undefined) {
        cookies.set("refresh_token", "signedout");
        window.location.reload();
      }

      const formData = new FormData();
      formData.set("grant_type", "refresh_token");
      formData.set("refresh_token", this.cookie.get("refresh_token"));

      axios
        .post(this.tokenEndpoint, formData, refreshTokenConfig)
        .then(response => {
          let accessTokenExpireDate = new Date();
          accessTokenExpireDate.setTime(
            accessTokenExpireDate.getTime() + 60 * 60 * 1000
          );
          this.cookie.set("access_token", response.data.access_token, {
            path: "/",
            expires: accessTokenExpireDate
          });
          error.config.headers["Authorization"] =
            "Bearer " + this.cookie.get("access_token");
          window.location.reload();
        })
        .catch(error => {
          this.cookie.remove("refresh_token");
          this.cookie.remove("access_token");
          window.location.reload();
        });
    }

    return Promise.reject(error);
  });
}
