import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { AuthenticationService } from './auth/auth';
import { Observable, from, of } from 'rxjs';
import { Comment } from "./comment";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private commentsUrl: string;
  private headers: HttpHeaders;

  constructor(private http: HttpClient, private authService: AuthenticationService) {
    this.commentsUrl = environment.backendUrl + "/comments";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public findAll(): Observable<Comment[]> {
    return this.http.get<Comment[]>(this.commentsUrl, {headers: this.headers});
  }

  public findById(id: string): Observable<Comment> {
    return this.http.get<Comment>(this.commentsUrl+"/"+id, {headers: this.headers});
  }

  public findByIds(ids: Array<string>): Observable<Comment[]> {
    var comments = Array<Comment>();
    ids.forEach(id => {
      this.findById(id).subscribe(found => {
        comments.push(found);
      });
    });
    console.log(comments);
    return of(comments);
  }

  public save(comment: Comment) {
    return this.http.post<Comment>(this.commentsUrl, comment, {headers: this.headers});
  }

  public update(comment: Comment) {
    return this.http.put<Comment>(this.commentsUrl, comment, {headers: this.headers});
  }

  public delete(comment: Comment) {
    return this.http.delete<Comment>(this.commentsUrl + "/" + comment.id, {headers: this.headers});
  }
}
