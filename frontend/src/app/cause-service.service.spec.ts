import { TestBed } from '@angular/core/testing';

import { CauseService } from './cause-service.service';

describe('CauseServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CauseService = TestBed.get(CauseService);
    expect(service).toBeTruthy();
  });
});
