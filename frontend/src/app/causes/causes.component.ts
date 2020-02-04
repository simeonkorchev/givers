import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../auth/auth';
import 'rxjs/add/operator/map';
import { CauseService } from '../cause-service.service';
import { AlertService } from '../alert-service';
import { Cause } from "../cause"
import { CollectorService } from '../collector.service';
import { EventType } from '../event-type.enum';
import { RecommenderService } from '../recommender-service.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-causes',
  templateUrl: './causes.component.html',
  styleUrls: ['./causes.component.css']
})
export class CausesComponent implements OnInit {
  public defaultImg: string = 'src/assets/placeholder.png';
  public entries: Array<any>;
  private causes: Observable<Cause[]>;
  private userLogs = [];

  constructor(
    private router: Router, 
    private alertService: AlertService,
    private causeService: CauseService,
    private authService: AuthenticationService,
    private collectorService: CollectorService,
    private recommenderService: RecommenderService
    ) {
    this.entries = [];
  }

  ngOnInit() {
    this.causes = this.causeService.findAll();
    this.updateLogsCount();
  }

  create() {
    this.router.navigate(["/cause"]);
  }

  attendToCause(cause: Cause) {
    var username = localStorage.getItem('username');
    this.causeService.attendToCause(cause, username)
      .subscribe(c => {
        this.collectorService.collect(username, cause.id, EventType.ATTEND, cause.name);
        this.alertService.success("Поздравления за участието!");
      }, err => {
        this.alertService.error("Случи се грешка: " + err);
      })
  }

  getImage(cause: Cause): string {
    return this.causeService.getImage(cause.id);
  }

  goToCommentDetails(cause: Cause) {
    this.router.navigate(["/cause/details", cause.id]);
  }
  
  logout() {
    this.authService.logout()
  }

  updateLogsCount() {
    this.userLogs = [];
    this.collectorService
      .findByUsername(localStorage.getItem('username'))
      .subscribe(logs => {
        this.userLogs = logs;
      })
  }

  checkLogsCount(): boolean {
    return this.userLogs.length < 5;
  }
  
  recommend() {
    this.causes = this.recommenderService.getRecommendations(localStorage.getItem('username'), 10);
  }

  goToLogs() {
    this.router.navigate(["/logs"]);
  }

  fitContent(name: string): string {
    var cut = "";
    if(name.length > 33) {
      cut = name.substring(0, 31);
      cut += "..";
    } else {
      cut = name;
    }
    return cut;
  }
}
