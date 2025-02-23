import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthSignupComponent } from './auth-signup/auth-signup.component';
import { authRoutes } from './auth.routes';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AuthSignupComponent
  ],
  imports: [
    HttpClientModule,
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(authRoutes)
  ],
  exports: [
    AuthSignupComponent
  ]
})
export class AuthModule { }
