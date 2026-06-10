import { describe, it, expect, beforeEach, vi } from 'vitest'
import { API } from '../config.js'

describe('Config API URLs', () => {
  it('should define base API URL', () => {
    expect(API).toBeDefined()
    expect(typeof API).toBe('object')
  })

  it('should have all required API endpoints', () => {
    expect(API.user).toBeDefined()
    expect(API.task).toBeDefined()
    expect(API.shop).toBeDefined()
    expect(API.achievement).toBeDefined()
    expect(API.points).toBeDefined()
    expect(API.file).toBeDefined()
  })

  it('should have correct base paths', () => {
    expect(API.user).toContain('/api/user')
    expect(API.task).toContain('/api/task')
    expect(API.points).toContain('/api/points')
  })
})
