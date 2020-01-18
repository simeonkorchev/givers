import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Log } from "./log";
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CollectorService {
  private collectUrl: string;
  private headers: HttpHeaders;

  constructor(
    private httpClient: HttpClient
  ) { 
    this.collectUrl = environment.backendUrl + "/collect";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public collect(username: string, causeId: string, event: string, causeName: string) {
    this.httpClient
      .post<Log>(this.collectUrl, new Log(null, username, causeId, event, causeName), {headers: this.headers})
      .subscribe(log => console.log(log));
  }

  public findByUsername(username: string): Observable<Log[]> {
    return this.httpClient.get<Log[]>(this.collectUrl + "/" + username, {headers: this.headers});
  }
}
