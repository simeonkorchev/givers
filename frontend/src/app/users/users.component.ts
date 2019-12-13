import { Component, OnInit } from '@angular/core';
import {UserService} from '../user-service.service';
import { AlertService } from '../alert-service';
import { User } from '../user';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  private userObservable: Observable<User>;
  private user: User;
  private userProfileForm: FormGroup;
  ready = true;
  submitted = false;
  
  constructor(
    private userService: UserService,
    private alertService: AlertService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
      this.userProfileForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.pattern("[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$")]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', Validators.required],
    });
    this.userObservable = this.userService.findByUsername(localStorage.getItem('username')).pipe(
    tap(user => {
      this.userProfileForm.patchValue(user[0]); //TODO might research and fix the array returned by the backend
    }, err => {
      this.alertService.error(err);
    })
    );
    this.userObservable.subscribe(u => {
      this.user = u[0];
    });
  }

  OnSubmit() {
    this.submitted = true; 
    if (this.userProfileForm.invalid) {
      return
    }
    this.ready = false;
    this.userService.update(this.userProfileForm.value).subscribe(
     user => {
        this.alertService.success("Profile updated successfully")
        this.ready = true;
     }, err => {
       this.alertService.error(err);
       this.ready = true;
     }
    );
  }
}
