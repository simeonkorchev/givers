import { Injectable } from "@angular/core";
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from '../auth/auth';


@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {
    
    constructor(
        private router: Router,
        private authService: AuthenticationService,
    ){}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if(this.authService.isLoggedIn()) {
            return true;
        }

        this.router.navigate(['/login']);
        return false;
    }

}