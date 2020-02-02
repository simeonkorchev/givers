import { Component, OnInit } from '@angular/core';
import {UserService} from '../user-service.service';
import { AlertService } from '../alert-service';
import { User } from '../user';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
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
  selectedFiles: FileList;  

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
      id: ['', Validators.required],
      photoPath: new FormControl()
    });
    this.userObservable = this.userService.findByUsername(localStorage.getItem('username')).pipe(
      tap(user => {
        this.userProfileForm.patchValue(user[0]);
      }, err => {
        this.alertService.error(err);
      })
    );
    this.userObservable.subscribe(u => {
      this.user = u[0];
    });
  }

  selectFile(event) {
    const file = event.target.files.item(0);
    var size = event.target.files[0].size;  
    if(size > 5000000) {  
      alert("size must not exceeds 5 MB");  
      this.userProfileForm.get('causeImage').setValue("");  
    }  
    else {  
      this.selectedFiles = event.target.files;  
    }
  }

  OnSubmit() {
    this.submitted = true; 
    if (this.userProfileForm.invalid) {
      return
    }
    this.ready = false;
    this.user.firstName = this.userProfileForm.get('firstName').value;
    this.user.lastName = this.userProfileForm.get('lastName').value;
    this.user.email = this.userProfileForm.get('email').value;
    this.userService.update(this.user).subscribe(
     user => {
        if(this.selectedFiles != null) {
          this.userService
            .uploadImage(this.selectedFiles.item(0), user.username)
            .subscribe(res => {
              this.alertService.success("Успешно обновихте профила си!")      
            }, err => {
              this.alertService.error("Не успяхме да обновим профилната Ви снимка. Моля, опитайте пак!");
            });
        }
        this.alertService.success("Успешно обновихте профила си!")
        this.ready = true;
     }, err => {
       this.alertService.error(err);
       this.ready = true;
     }
    );
  }

  getUserImage(): string {
    return this.userService.getImage(this.user.username);
  }
}
