import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthenticationService } from './auth/auth';
import { Observable } from 'rxjs';
import { User } from "./user";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private usersUrl: string;
  private headers: HttpHeaders;

  constructor(private http: HttpClient, private authService: AuthenticationService) {
    this.usersUrl = environment.backendUrl + "/users";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl, {headers: this.headers});
  }
  public findById(id: string): Observable<User> {
    return this.http.get<User>(this.usersUrl+"/" + id, {headers: this.headers});
  }
  public findByUsername(username: string): Observable<User> {
    return this.http.get<User>(this.usersUrl+"/user/"+username, {headers: this.headers});
  }

  public save(user: User): Observable<User> {
    return this.http.post<User>(this.usersUrl, user, {headers: this.headers});
  }

  public update(user: User) {
    return this.http.put<User>(this.usersUrl, user, {headers: this.headers});
  }

  public delete(user: User): Observable<User> {
    return this.http.delete<User>(this.usersUrl + "/" + user.id, {headers: this.headers});
  } 
  public register(user: User): Observable<User> {
    return this.http.post<User>(this.usersUrl, user, {headers: this.headers});
  }
  public changePassword(user: User, oldPassword: string, newPassword: string): Observable<User> {
    return this.http.put<User>(this.usersUrl + "/updatePassword?oldPassword="+oldPassword+"&newPassword="+newPassword, user);
  }

  public uploadImage(file: File, id: String): Observable<any> {
    const formData: FormData = new FormData();
    formData.append("file", file);
    return this.http.post(this.usersUrl + "/upload/" + id, formData);
  }

  public getImage(owner: string): string {
    return this.usersUrl + "/image/" + owner;
  }
}
