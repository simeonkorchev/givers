import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { Cause } from '../cause';

@Component({
  selector: 'app-user-own-causes',
  templateUrl: './user-own-causes.component.html',
  styleUrls: ['./user-own-causes.component.css']
})
export class UserOwnCausesComponent implements OnInit {
  private ownCauses: Cause[];

  constructor(
    private route: ActivatedRoute, 
    private causeService: CauseService
    ) {
  }

  ngOnInit() {
    this.route.params
        .map(params => params['userId'])
        .pipe(
          switchMap(id => {
            return this.causeService.findOwnCauses(id);
          })
        )
        .subscribe(causes => this.ownCauses = causes);
  }

}
