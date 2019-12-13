import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserOwnCausesComponent } from './user-own-causes.component';

describe('UserOwnCausesComponent', () => {
  let component: UserOwnCausesComponent;
  let fixture: ComponentFixture<UserOwnCausesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserOwnCausesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserOwnCausesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
