import Cookies from "universal-cookie";

export function getToken() {
  const cookies = new Cookies();
  return cookies.get("access_token")
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
