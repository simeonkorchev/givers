import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { Cause } from './cause';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class RecommenderService {
  private recommendUrl: string;
  private headers: HttpHeaders;

  constructor(private http: HttpClient) {
    this.recommendUrl = environment.backendUrl + "/recommend";
    this.headers = new HttpHeaders({ "content-type": "application/json"});
  }

  public getRecommendations(username: string, count: number): Observable<Cause[]> {
    return this.http.get<Cause[]>(this.recommendUrl + "/" + username + "?"+"count="+ count, {headers: this.headers});
  }

}
