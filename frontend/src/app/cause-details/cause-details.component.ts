import { Component, OnInit, OnDestroy } from '@angular/core';
import { Cause } from '../cause';
import { CauseService } from '../cause-service.service';
import { AlertService } from '../alert-service';
import { CommentService } from '../comment-service.service';
import { Comment } from '../comment';
import { ActivatedRoute } from '@angular/router';
import { switchMap, map, concatMap } from 'rxjs/operators';
import { pipe } from 'rxjs';
import { CollectorService } from '../collector.service';
import { EventType } from '../event-type.enum';

@Component({
  selector: 'app-cause-details',
  templateUrl: './cause-details.component.html',
  styleUrls: ['./cause-details.component.css']
})
export class CauseDetailsComponent implements OnInit  {

  cause: Cause;
  isLoaded = false;
  comment= "";
  alreadyMadeComments: Array<Comment> = [];
  private sub: any;
  private id: string;

  constructor(
    private causeService: CauseService,
    private alertService: AlertService,
    private commentService: CommentService,
    private collectorService: CollectorService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.route.params
      .map(params => params['id'])
      .pipe(
        switchMap(id => {
          if(id != null && id !== undefined) {
            return this.causeService.findById(id);
          }
        })
      ).subscribe(causes => {
        this.cause = causes[0];
        this.collectorService.collect(localStorage.getItem('username'), this.cause.id, EventType.CAUSE_DETAILS_VIEWED, this.cause.name);
        this.cause.commentIds.forEach(id => {
          this.commentService.findById(id)
            .subscribe(u =>
              this.alreadyMadeComments.push(u)
            );
          });
      });
    // this.sub = this.route.params.subscribe(params => {
    //   this.id = params['id'];
    //   this.causeService.findById(this.id).subscribe(causes => {
    //     this.cause = causes[0];
    //     this.commentService.findByIds(this.cause.commentIds).subscribe(comments => {
    //       this.alreadyMadeComments = comments;
    //     });
    //     console.log(this.alreadyMadeComments);
    // });
  // });
}

  loadComments() {
    this.cause.commentIds.forEach(commentId => {
      this.commentService.findById(commentId).subscribe(found => {
        console.log(found);
        this.alreadyMadeComments.push(found);
      });
    });
    console.log(this.alreadyMadeComments);
  }

  getImage(photoPath: string): string {
    if(photoPath == undefined || photoPath == null) {
      return "./assets/placeholder.jpg";
    }
    return photoPath;
   }

   attendToCause() {
    var username = localStorage.getItem('username')
    this.causeService.attendToCause(this.cause, username)
      .subscribe(c => {
        this.collectorService.collect(username, this.cause.id, EventType.ATTEND, this.cause.name);
        this.alertService.success("Congratulations for getting involved!");
      }, err => {
        this.alertService.error("Something went wrong: " + err);
      })
  }

   addComment() {
     if(this.comment == "" || this.comment == " ") {
       return;
     }
     var c = new Comment(null, localStorage.getItem('username'), this.cause.id, this.comment);
     this.commentService.save(c).subscribe(
       res => {
         this.alertService.success("Comment successfull");
       }, err => {
         this.alertService.error(err);
       }
     )
   }
}
