import { TestBed } from '@angular/core/testing';

import { RecommenderServiceService } from './recommender-service.service';

describe('RecommenderServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RecommenderService = TestBed.get(RecommenderService);
    expect(service).toBeTruthy();
  });
});
