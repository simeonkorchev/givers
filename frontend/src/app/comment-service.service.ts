import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { AuthenticationService } from './auth/auth';
import { Observable, from, of } from 'rxjs';
import { Comment } from "./comment";
import { environment } from '../environments/environment';
import * as Rx from "rxjs/Rx";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private commentsUrl: string;
  private commentWsUrl: string;
  private headers: HttpHeaders;
  private subject: Rx.Subject<MessageEvent>;

  constructor(private http: HttpClient, private authService: AuthenticationService) {
    this.commentsUrl = environment.backendUrl + "/comments";
    this.commentWsUrl = environment.backendUrl.replace("http", "ws") + "/ws/comments";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public connect(): Rx.Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create();
      console.log("Successfully connected: " + this.commentWsUrl);
    }
    return this.subject;
  }

  private create(): Rx.Subject<MessageEvent> {
    let ws = new WebSocket(this.commentWsUrl);

    let observable = Rx.Observable.create((obs: Rx.Observer<MessageEvent>) => {
      ws.onmessage = obs.next.bind(obs);
      ws.onerror = obs.error.bind(obs);
      ws.onclose = obs.complete.bind(obs);
      return ws.close.bind(ws);
    });
    let observer = {
      next: (data: Object) => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
        }
      }
    };
    return Rx.Subject.create(observer, observable);
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
