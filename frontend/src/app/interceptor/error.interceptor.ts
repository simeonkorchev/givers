import { Injectable } from "@angular/core";
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthenticationService } from '../auth/auth';
import { AlertService } from '../alert-service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authService: AuthenticationService, private alertService: AlertService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if(!err) {
                return
            }
            if(err.status==401) {
                this.authService.logout();
                this.alertService.error("Invalid username or password.");
                return;
            }
            const error = err.error.message || err.statusText;
            return throwError(error);
        }))
    } 
}