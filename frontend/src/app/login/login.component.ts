import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../auth/auth';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AlertService } from '../alert-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string;

  constructor(private route: ActivatedRoute, private formBuilder: FormBuilder,private router: Router, private authService: AuthenticationService, private alertService: AlertService) {
    if (authService.isLoggedIn()) {
      this.router.navigate(["/causes"]);
    }
   }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
    // get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  login() {
    this.loading = true;
    this.authService.login(this.f.username.value, this.f.password.value)
      .then(
        data => {
          this.alertService.success("Успешно влезнахте в системата!");
          this.router.navigate(["/causes"]);
        },
      ).catch(error => {
          this.alertService.error("Невалидно потребителско име или парола");
          this.loading = false;
        }
      );
      this.loading = false;
  }

  // convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }

  async onSubmit() {
    this.submitted = true;
    if(this.loginForm.invalid) {
      return;
    }
    this.login();
  }
}
