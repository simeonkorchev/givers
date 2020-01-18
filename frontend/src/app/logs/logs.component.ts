import { Component, OnInit } from '@angular/core';
import { CollectorService } from '../collector.service';
import { AlertService } from '../alert-service';
import { Log } from '../log';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit {
  logs: Log[] = [];
  columns = ['ID', "Кауза", "Събитие", "Потребител"];
  index = ['id', 'causeName', 'eventType', 'username'];

  constructor(
    private collectorService: CollectorService,
    private alertService: AlertService,
  ) { }

  ngOnInit() {
    this.collectorService
      .findByUsername(localStorage.getItem('username'))
      .subscribe(logs => {
        this.logs = logs;
        console.log(logs);
      });
  }

}
