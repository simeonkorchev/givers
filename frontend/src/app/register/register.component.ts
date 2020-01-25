import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AlertService } from '../alert-service';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { UserService } from '../user-service.service';
import { User } from '../user';

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
  selectedFiles: FileList;  
  currentFileUpload: File;

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
        authorities: [Array("ROLE_USER")],
        photoPath: new FormControl()
    });
  }

  selectFile(event) {
    var size = event.target.files[0].size;  
    if(size > 5000000) {  
      alert("size must not exceeds 5 MB");  
      this.registerForm.get('userImage').setValue("");  
    }  
    else {  
      this.selectedFiles = event.target.files;  
    } 
  }

  get f() { return this.registerForm.controls; }

  onSubmit() { 
    this.submitted = true; 
    if (this.registerForm.invalid) {
      return
    }
    this.loading = true;
    var username = this.registerForm.get('username').value;
    var password = this.registerForm.get('password').value;
    var firstName = this.registerForm.get('firstName').value;
    var lastName = this.registerForm.get('lastName').value;
    var email = this.registerForm.get('email').value;
    var causes = null;
    var ownCauses = null;
    var authorities = Array("ROLE_USER");
    var photoPath: string;
    var honor = 0;

    if(this.selectedFiles == undefined || this.selectedFiles.length == 0) {
      photoPath = "./assets/avatar.jpg";
    }

    var user = new User({
      id: null,
      username: username,
      password: password,
      firstName: firstName,
      lastName: lastName,
      causes: causes,
      ownCauses: ownCauses,
      authorities: authorities,
      email: email,
      photoPath: photoPath,
      honor: honor
    });

    this.userService.register(user).subscribe(
      user => {
        if(this.selectedFiles != null) {
          this.userService.uploadImage(this.selectedFiles.item(0), user.username)
            .subscribe(() => {
              this.alertService.success('Регистрацията е успешна!', true);
              this.router.navigate(["/login"]);
              this.loading = false;
            }, err => {
              this.alertService.error("Не можахме да направим вашата регистрация, моля опитайте по-късно!");
              this.loading = false;
              console.log(err);
            })
        } else {
          this.alertService.success('Регистрацията е успешна!', true);
          this.router.navigate(["/login"]);
          this.loading = false;
        }
      }, error => {
        console.log(error);
        this.alertService.error(error);
        this.loading = false;
      });
  }
}
