import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { provideHttpClient } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent], // ✅ `AppComponent` est standalone, pas besoin d'autres imports
      providers: [provideHttpClient()], // ✅ Nouvelle manière d'injecter HttpClient
      schemas: [NO_ERRORS_SCHEMA] // ✅ Ignore les erreurs de composants inconnus
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display the expected messages', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain("J'attends le message");
  });
});
