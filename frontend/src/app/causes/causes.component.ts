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
import { environment } from '../../environments/environment'

@Component({
  selector: 'app-causes',
  templateUrl: './causes.component.html',
  styleUrls: ['./causes.component.css']
})
export class CausesComponent implements OnInit {
  public defaultImg: string = 'src/assets/placeholder.png';
  public entries: Array<any>;
  private causes: Observable<Cause[]>;

  constructor(
    private router: Router, 
    private route: ActivatedRoute,
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
   if(cause.photoPath == undefined || cause.photoPath == null) {
    return environment.imagesMount + cause.id;
   }
   return cause.photoPath;
  }

  goToCommentDetails(cause: Cause) {
    this.router.navigate(["/cause/details", cause.id]);
  }
  
  logout() {
    this.authService.logout()
  }

  recommend() {
    this.causes = this.recommenderService.getRecommendations(localStorage.getItem('username'), 10);
  }
}
