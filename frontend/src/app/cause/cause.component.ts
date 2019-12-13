import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlertService } from '../alert-service';

@Component({
  selector: 'app-cause',
  templateUrl: './cause.component.html',
  styleUrls: ['./cause.component.css']
})

export class CauseComponent implements OnInit {
  private causeTypes = ['Children', 'Adults', 'Homeless', 'Animals', 'Nature'];
  causeForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;

  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private causeService: CauseService,
    private formBuilder: FormBuilder,
    private alertService: AlertService) {
  }

  ngOnInit() {
    this.causeForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      time: ['', Validators.required],
      causeType: ['', Validators.required]
    });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  get f() { return this.causeForm.controls; }

  private goToCausesList() {
    this.router.navigate(['/causes']);
  }

  OnSubmit() {
    this.submitted = true;
    if (!this.causeForm.valid) {
      return
    }
    this.causeService.save(this.causeForm.value).subscribe(
      cause => {
        this.alertService.success("Cause has been created successfully!");
        this.goToCausesList();
      }, err => {
        this.alertService.error("Could not create the cause: " + err);
      }
    );
  }
}
