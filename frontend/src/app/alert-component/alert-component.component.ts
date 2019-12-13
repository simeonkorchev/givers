import { Component, OnInit, OnDestroy } from '@angular/core';
import { AlertService } from '../alert-service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'alert',
  templateUrl: './alert-component.component.html',
  styleUrls: ['./alert-component.component.css']
})
export class AlertComponentComponent implements OnInit, OnDestroy {
  private subscription: Subscription;
  message: any;

  constructor(private alertService: AlertService) { }

  ngOnInit() {
    this.subscription = this.alertService.getMessage().subscribe(message => {
      this.message = message;
    })
  } 

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
