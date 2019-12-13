import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserParticipationComponent } from './user-participation.component';

describe('UserParticipationComponent', () => {
  let component: UserParticipationComponent;
  let fixture: ComponentFixture<UserParticipationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserParticipationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserParticipationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
