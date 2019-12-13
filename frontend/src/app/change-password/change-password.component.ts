import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlertService } from '../alert-service';
import { UserService } from '../user-service.service';
import { Observable } from "rxjs";
import { User } from "../user";
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;
  user: User;
  
  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private alertService: AlertService,
    private userService: UserService) {
    
  }

  ngOnInit() {
    this.changePasswordForm = this.formBuilder.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]] ,
      newPasswordConfirm: ['', [Validators.required,Validators.minLength(6)]]
    });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  changePassword() {
    this.loading = true;
    this.userService.findByUsername(localStorage.getItem('username'))
    .subscribe( user => {
        this.user = user[0];
        console.log(user);
        this.userService.changePassword(user[0], this.f.oldPassword.value, this.f.newPassword.value)
          .subscribe(user => {
            this.alertService.success("Password is updated successfully");
            this.loading = false;
          }, err => {
            this.alertService.error(err);
          });
      }, err => {
        this.alertService.error(err);
      });
    this.loading = false;
  }

  // convenience getter for easy access to form fields
  get f() { return this.changePasswordForm.controls; }

  onSubmit() {
    this.submitted = true;
    if(this.changePasswordForm.invalid) {
      return;
    }
    this.changePassword();
  }
}
