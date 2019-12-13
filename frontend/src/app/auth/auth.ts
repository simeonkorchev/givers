import { Injectable } from "@angular/core";
import * as moment from "moment";
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/shareReplay';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable()
export class AuthenticationService {
    authUrl: string;

    constructor(private http: HttpClient) {
        this.authUrl = environment.backendUrl + "/login";
    }

    async login(username: string, password: string) {
        //TODO make this in reactive way, e.g. replace .toPromise() with .subscribe()
        let headers = new HttpHeaders({"content-type": "application/json"});
        await this.http
        .post(this.authUrl, {"username":username, "password": password}, {headers}).toPromise()
        .then(user => {
            if(user) {
                this.setSession(user);
            }
            return user;
        });
    }

    private setSession(authResult) {
        localStorage.setItem('id_token', authResult.token);
        localStorage.setItem('username', authResult.username);
    }

    logout() {
        localStorage.removeItem('id_token');
        localStorage.removeItem('username');
    }

    public getToken(): string {
        return localStorage.getItem("id_token");
    }

    public getUsername(): string {
        return localStorage.getItem('username');
    }

    public isLoggedIn() {
        var token = this.getToken()
        return token != null || token != undefined;
    }

    public isLoggedOut() {
        return !this.isLoggedIn();
    }

    getExpiration() {
        const expiration = localStorage.getItem("expires_at");
        const expiresAt = JSON.parse(expiration);
        return moment(expiresAt);
    }
}