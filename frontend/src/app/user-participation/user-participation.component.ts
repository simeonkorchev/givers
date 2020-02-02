import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Cause } from '../cause';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-user-participation',
  templateUrl: './user-participation.component.html',
  styleUrls: ['./user-participation.component.css']
})
export class UserParticipationComponent implements OnInit {
  private participatedCauses: Cause[];
  
  constructor(
    private router: Router, 
    private route: ActivatedRoute, 
    private causeService: CauseService
  ) { }

  ngOnInit() {
    this.route.params
      .map(params => params['userId'])
      .pipe(
        switchMap(id => {
          return this.causeService.getUserParticipation(localStorage.getItem('username'));
        })
      ).subscribe(causes => {
        this.participatedCauses = causes;
      })
  }

  getImage(cause: Cause): string {
    return this.causeService.getImage(cause.id);
  }

  goToCommentDetails(cause: Cause) {
    this.router.navigate(["/cause/details", cause.id]);
  }

  fitContent(name: string): string {
    var cut = "";
    if(name.length > 35) {
      cut = name.substring(0, 33);
      cut += "..";
    } else {
      cut = name;
    }
    return cut;
  }
}
