import Cookies from 'universal-cookie';

class Rest {
    constructor() {
        const host = "http://localhost:8080/";
        
        this.login = host + "oauth/token";
        this.logged = host + "api/user/logged";
        this.register = host + "api/user/register";
        this.logout = host + "api/user/logout";

        this.datasets = host + "api/data/datasets";
        this.addData = host + "api/data/add";
        this.downloadData = host + "api/data/getdata";
        this.cookie = new Cookies();
    }

    getLoginToken(username, password, onSuccessFunction) {
        const data = {
            'username': username, 
            'password': password,
            'grant_type': "password"
        };

        let formData = new FormData();
        for(let d in data) {
            formData.append(d, data[d]);
        }

        fetch(this.login, {
            method: 'POST',
            body: formData,
            headers:{
              'Authorization': "Basic " + btoa("bathymetry:bathymetry")
            }
          }).then(res => {
              console.log(res.status);

              if(res.status === 200) {
                res.json().then(response => {
                    let accessTokenExpireDate = new Date();
                    accessTokenExpireDate.setTime(accessTokenExpireDate.getTime() + 60*60*1000)
                    this.cookie.set("access_token", response.access_token, {path: '/', expires: accessTokenExpireDate});
                    
                    let refreshTokenExpireDate = new Date();
                    refreshTokenExpireDate.setTime(refreshTokenExpireDate.getDate + 24*60*60*1000);
                    this.cookie.set("refresh_token", response.refresh_token, {path: '/', expires: refreshTokenExpireDate});
                    onSuccessFunction();
                });
              }

          });
    }

    instantLogin(onSuccessFunction) {
        fetch(this.logged, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        }).then(res => {
            console.log('login: ' + res.status);
            if(res.status === 200) {
                onSuccessFunction();
            }
        });
    }


}

export const RestFetch = new Rest();