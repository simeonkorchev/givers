import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthenticationService } from '../auth/auth';
import 'rxjs/add/operator/map';
import { CauseService } from '../cause-service.service';
import { AlertService } from '../alert-service';
import { Cause } from "../cause"
import { CollectorService } from '../collector.service';
import { EventType } from '../event-type.enum';

@Component({
  selector: 'app-causes',
  templateUrl: './causes.component.html',
  styleUrls: ['./causes.component.css']
})
export class CausesComponent implements OnInit {
  public defaultImg: string = '/Users/i340033/Documents/givers-frontend/src/assets/placeholder.png';
  public entries: Array<any>;

  constructor(
    private router: Router, 
    private route: ActivatedRoute,
    private alertService: AlertService,
    private causeService: CauseService,
    private authService: AuthenticationService,
    private collectorService: CollectorService
    ) {
    this.entries = [];
  }

  ngOnInit() {
    //TODO rewrite this in reactive whay, e.g. subscribe to the returned Observable
    this.causeService.findAll().toPromise()
    .then(result => { 
        this.entries = result;
    }).catch(err => {
      this.alertService.error(err);
    });
    //TODO - add support for WebSockets
  }

  create() {
    this.router.navigate(["/cause"]);
  }

  attendToCause(cause: Cause) {
    var username = localStorage.getItem('username');
    this.collectorService.collect(username, cause.id, EventType.ATTEND, cause.name);
    this.causeService.attendToCause(cause, username)
      .subscribe(c => {
        this.alertService.success("Congratulations for getting involved!");
      }, err => {
        this.alertService.error("Something went wrong: " + err);
      })
  }

  getImage(photoPath: string): string {
   if(photoPath == undefined || photoPath == null) {
     return "./assets/placeholder.jpg";
   }
   return photoPath;
  }

  goToCommentDetails(cause: Cause) {
    this.router.navigate(["/cause/details", cause.id]);
  }
  
  logout() {
    this.authService.logout()
  }
}
