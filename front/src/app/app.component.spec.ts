import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth/auth.service';
import { Router } from '@angular/router';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['logout']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, // ✅ Fournit Router
        AppComponent
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService } // ✅ Fournit mock AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router); // ✅ Injecte le Router proprement
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should read user_name from localStorage on init', () => {
    localStorage.setItem('user_name', 'Sidi');
    component.ngOnInit();
    expect(component.userName).toBe('Sidi');
  });

  it('should call AuthService.logout and navigate on logout()', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.logout();
    expect(mockAuthService.logout).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['/auth/signin']);
  });
});
