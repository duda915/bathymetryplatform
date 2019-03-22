import Cookies from "universal-cookie";

export function getToken() {
  const cookies = new Cookies();
  if (cookies.get("access_token")) {
    this.api
      .restUser()
      .getUser()
      .then(() => this.props.signIn())
      .catch(() => console.log("auto login not possible"));
  }
}

export function saveTokens(response) {
  const cookies = new Cookies();

  const accessTokenExpireDate = new Date();
  accessTokenExpireDate.setDate(
    accessTokenExpireDate.getDate() + 60 * 60 * 1000
  );
  cookies.set("access_token", response.data.access_token, {
    expires: accessTokenExpireDate
  });

  const refreshTokenExpireDate = new Date();
  refreshTokenExpireDate.setDate(
    refreshTokenExpireDate.getDate() + 24 * 60 * 60 * 1000
  );
  cookies.set("refresh_token", response.data.refresh_token, {
    expires: refreshTokenExpireDate
  });
}
