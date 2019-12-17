import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { CausesComponent } from './causes/causes.component';
import { UsersComponent } from './users/users.component';
import { HttpClientModule } from '@angular/common/http';
import { CauseComponent } from './cause/cause.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthenticationService } from './auth/auth';
import { httpInterceptorProviders } from './interceptor';
import { AlertComponentComponent } from './alert-component/alert-component.component';
import { AlertService } from './alert-service';
import { AuthGuard } from './guards/auth.guard';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { faUsersCog, faPowerOff, faPlusCircle, faHandHoldingHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule, FaIconLibrary, } from '@fortawesome/angular-fontawesome';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { MatDatepickerModule, MatNativeDateModule, MatInputModule } from '@angular/material';
import { CauseDetailsComponent } from './cause-details/cause-details.component';
import { UserOwnCausesComponent } from './user-own-causes/user-own-causes.component';
import { UserParticipationComponent } from './user-participation/user-participation.component';
import { RecommendationsComponent } from './recommendations/recommendations.component';

let routes = [
  { path: "", redirectTo: "/login", pathMatch: "full" },
  { path: "register", component: RegisterComponent },
  { path: "login", component: LoginComponent },
  { path: "user", component: UsersComponent, canActivate: [AuthGuard] },
  { path: "causes", component: CausesComponent, canActivate: [AuthGuard] },
  { path: "cause", component: CauseComponent, canActivate: [AuthGuard] },
  { path: "user/security", component: ChangePasswordComponent, canActivate: [AuthGuard] },
  { path: "user/my/causes/:userId", component: UserOwnCausesComponent, canActivate: [AuthGuard] },
  { path: "user/attend/causes/:userId", component: UserParticipationComponent, canActivate: [AuthGuard] },
  { path: "cause/details/:id", component: CauseDetailsComponent, canActivate: [AuthGuard] }
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    UsersComponent,
    CausesComponent,
    CauseComponent,
    AlertComponentComponent,
    ChangePasswordComponent,
    CauseDetailsComponent,
    UserOwnCausesComponent,
    UserParticipationComponent,
    RecommendationsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    BrowserAnimationsModule,
    FontAwesomeModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule
  ],
  providers: [
    AuthenticationService,
    AlertService,
    httpInterceptorProviders,
    AuthGuard
  ],
  bootstrap: [AppComponent]
})

export class AppModule {
  constructor(library: FaIconLibrary) {
    library.addIcons(faUsersCog, faPowerOff, faPlusCircle, faHandHoldingHeart);
  }
}
