import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import { Cause } from './cause';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../environments/environment';
import {EventSourcePolyfill} from 'ng-event-source';
@Injectable({
  providedIn: 'root'
})
export class CauseService {
  private causesUrl: string;
  private headers: HttpHeaders;
  private causes: Cause[] = [];

  constructor(private http: HttpClient) {
    this.causesUrl = environment.backendUrl + "/causes";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public findAll(): Observable<Array<Cause>> {
    this.causes = [];
    return Observable.create((observer) => {
      let url = this.causesUrl;
      let causeEventSource = new EventSourcePolyfill(url,{ headers: { "Authorization": "Bearer " + localStorage.getItem("id_token")}});
      causeEventSource.onmessage = (event) => {
        console.debug("Received event: ", event);
        let json = JSON.parse(event.data);
        this.causes.push(new Cause(json));
        observer.next(this.causes);
      }

      causeEventSource.onerror = (error) => {
        if(causeEventSource.readyState == 0) {
          console.log('The stream has been closed by the server.');
          causeEventSource.close();
          observer.complete();
        } else {
          observer.error('EventSource error: ' + error);
        }
      }
    })
  }
  
  public findById(id: string): Observable<Cause> {
    return this.http.get<Cause>(this.causesUrl+"/"+id, {headers: this.headers});
  }

  public save(cause: Cause) : Observable<Cause> {
    return this.http.post<Cause>(this.causesUrl, cause, {headers: this.headers});
  }

  public uploadImage(file: File, id: String): Observable<any> {
    const formData: FormData = new FormData();
    formData.append("file", file);
    return this.http.post(this.causesUrl + "/upload/" + id, formData);
  }
  
  public update(cause: Cause) {
    return this.http.put<Cause>(this.causesUrl, cause, {headers: this.headers});
  }

  public attendToCause(cause: Cause, username: string) {
    return this.http.put<Cause>(this.causesUrl + "/attend/"+ username, cause, {headers: this.headers});
  }

  public delete(cause: Cause) {
    this.http.delete<Cause>(this.causesUrl +"/"+ cause.id, {headers: this.headers});
  }

  public findOwnCauses(ownerId: string): Observable<Cause[]> {
    return this.http.get<Cause[]>(this.causesUrl+"/own/"+ ownerId, {headers: this.headers});
  }

  public getUserParticipation(id: string): Observable<Cause[]> {
    return this.http.get<Cause[]>(this.causesUrl + "/attend/" + id, {headers: this.headers});
  }

  getImage(id: string): string {
    return this.causesUrl + "/image/" + id;
  }
}
