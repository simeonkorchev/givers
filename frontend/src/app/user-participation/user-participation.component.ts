import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute } from '@angular/router';
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
    private route: ActivatedRoute, 
    private causeService: CauseService
  ) { }

  ngOnInit() {
    this.route.params
      .map(params => params['userId'])
      .pipe(
        switchMap(id => {
          return this.causeService.getUserParticipation(id)
        })
      ).subscribe(causes => {
        this.participatedCauses = causes;
      })
  }

}
