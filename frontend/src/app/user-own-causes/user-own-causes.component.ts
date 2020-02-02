import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { Cause } from '../cause';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-user-own-causes',
  templateUrl: './user-own-causes.component.html',
  styleUrls: ['./user-own-causes.component.css']
})
export class UserOwnCausesComponent implements OnInit {
  private ownCauses: Cause[];

  constructor(
    private router: Router,
    private route: ActivatedRoute, 
    private causeService: CauseService
    ) {
  }

  ngOnInit() {
    this.causeService
      .findOwnCauses(localStorage.getItem('username'))
      .subscribe(causes => {
        this.ownCauses = causes;
        console.log(causes);
      });
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
