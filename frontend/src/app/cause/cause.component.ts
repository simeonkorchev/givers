import { Component, OnInit } from '@angular/core';
import { CauseService } from '../cause-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { AlertService } from '../alert-service';
import { Cause } from '../cause';

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
  selectedFiles: FileList;  
  currentFileUpload: File;

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
      date: ['', Validators.required],
      owner: localStorage.getItem('username'),
      causeType: ['', Validators.required],
      photoPath: new FormControl()
    });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  get f() { return this.causeForm.controls; }

  private goToCausesList() {
    this.router.navigate(['/causes']);
  }

  selectFile(event) {
    const file = event.target.files.item(0);
    var size = event.target.files[0].size;  
    if(size > 5000000) {  
      alert("size must not exceeds 5 MB");  
      this.causeForm.get('causeImage').setValue("");  
    }  
    else {  
      this.selectedFiles = event.target.files;  
    }
  }

  OnSubmit() {
    this.submitted = true;
    if (!this.causeForm.valid) {
      return
    }
    var name = this.causeForm.get('name').value;
    var location = this.causeForm.get('location').value;
    var description = this.causeForm.get('description').value;
    var causeType = this.causeForm.get('causeType').value;
    var time = this.causeForm.get('date').value.getTime() / 1000;
    var imagePath: string;

    if(this.selectedFiles.length == 0) {
      imagePath = "./assets/placeholder.jpg";
    }
    var c = new Cause({
      name: name,
      location: location,
      description: description,
      causeType: causeType,
      time: time,
      imagePath: imagePath
    });

    this.causeService.save(c).subscribe(
      cause => {
        if(this.selectedFiles != null) {
          this.causeService.uploadImage(this.selectedFiles.item(0), cause.id).subscribe(res => {
            this.alertService.success("Успешно създадохте кауза!");
            this.goToCausesList();
          }, err => {
            this.alertService.error(err);
          })
        }
      }, err => {
        this.alertService.error("Каузата не може да бъде създадена: " + err);
      }
    );
  }
}
