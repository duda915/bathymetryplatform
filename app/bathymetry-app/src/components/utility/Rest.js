class Rest {
    constructor() {
        const host = "http://localhost:8080/";
        
        this.login = host + "oauth/token";
        this.logged = host + "api/user/logged";
        this.register = host + "api/user/register";

        this.datasets = host + "api/data/datasets";
        this.addData = host + "api/data/add";
        this.downloadData = host + "api/data/getdata";
    }
}

export const RestConfig = new Rest();