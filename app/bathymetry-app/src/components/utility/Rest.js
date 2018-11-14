import Cookies from 'universal-cookie';

class Rest {
    constructor() {
        const host = "http://51.38.132.245:8082/";
        this.geoserver = "http://51.38.132.245:8081/geoserver/bathymetry/wms?";
        
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

                    if(typeof onSuccessFunction === "function") {
                        onSuccessFunction();
                    }
                });
              }

          });
    }

    getUsername(onSuccessFunction, responseDataFunction) {
        fetch(this.logged, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        }).then(res => {
            console.log('login: ' + res.status);
            if(res.status === 200) {
                if(typeof onSuccessFunction === "function") {
                    onSuccessFunction();
                }
                res.text().then(val => {
                    if(typeof responseDataFunction === "function") {
                        responseDataFunction(val);
                    }
                })
            }
        });
    }

    sendLogout(onSuccessFunction) {
        fetch(this.logout, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        }).then(res => {
            this.cookie.remove("access_token", {path: '/'});
            this.cookie.remove("refresh_token", {path: '/'});
            
            if(typeof onSuccessFunction === "function") {
                onSuccessFunction();
            }
        });
    }

    getDataSets(onSuccessFunction) {
        fetch(this.datasets, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        }).then(res => {
            if(res.status === 200) {
                res.json().then(json => {
                    if(typeof onSuccessFunction === "function") {
                        onSuccessFunction(json);
                    }
                })
            }
        })
    }

    addDataRequest(params, file) {
        let url = new URL(this.addData);
        url.search = new URLSearchParams(params);
        let data = {
            file: file
        }

        let formData = new FormData();
        for(let d in data) {
            formData.append(d, data[d]);
        }

        fetch(url,{
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            },
            body: formData
        }).then(response => {
            console.log(response);
        })
    }

    downloadDataSet(id) {
        let url = new URL(this.downloadData);
        let params = {
            id: id
        }
        url.search = new URLSearchParams(params);

        let download = require("downloadjs");

        fetch(url, { 
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + this.cookie.get("access_token")
            }
        }).then(response => {
            if(response.status === 200){
                response.blob().then(blob => {
                    download(blob, "bathymetry"+id+".csv", "text/plain");
                })
            }
        })
    }


}

export const RestFetch = new Rest();