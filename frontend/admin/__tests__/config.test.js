import { describe, it, expect } from 'vitest'
import fs from 'fs'
import path from 'path'
import vm from 'vm'

const configPath = path.resolve(__dirname, '../config.js')

describe('Config API URLs', () => {
  const context = {}
  vm.createContext(context)

  const code = fs.readFileSync(configPath, 'utf8')
  vm.runInContext(code + '\nthis.API = API;', context)

  const API = context.API

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
    expect(API.shop).toContain('/api/shop')
    expect(API.achievement).toContain('/api/achievement')
    expect(API.points).toContain('/api/points')
  })
})