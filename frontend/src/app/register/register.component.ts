import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AlertService } from '../alert-service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../user-service.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  public input: any;
  registerForm: FormGroup;
  loading = false;
  submitted = false;

  constructor(
    private httpClient: HttpClient,
    private router: Router, 
    private alertService: AlertService, 
    private formBuilder: FormBuilder,
    private userService: UserService) { 
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
        email: ['', [Validators.required, Validators.pattern("[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$")]],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        username: ['', Validators.required],
        password: ['', [Validators.required, Validators.minLength(6)]],
        authorities: [Array("ROLE_USER")]
    });
}

// convenience getter for easy access to form fields
get f() { return this.registerForm.controls; }

  async onSubmit() { 
    this.submitted = true; 
    if (this.registerForm.invalid) {
      return
    }
    this.loading = true;  
    await this.userService.register(this.registerForm.value).toPromise()
      .then(
        data => {
          this.alertService.success('Registration successful', true);
          this.router.navigate(["/login"]);
          this.loading = false;
      },)
      .catch(error => {
        this.alertService.error(error);
        this.loading = false;
        
      })
      return;
  }
}
