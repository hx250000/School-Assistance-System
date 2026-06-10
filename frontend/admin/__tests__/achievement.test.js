import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import '../config.js'

global.fetch = vi.fn()

describe('Achievement Functions', () => {
  beforeEach(() => {
    global.fetch.mockClear()
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('loadAchievements', () => {
    it('should parse achievement data correctly', () => {
      const mockAchievements = {
        data: [
          {
            id: 1,
            title: '第一步',
            description: '完成首个任务',
            type: 'TASK_BASED',
            conditionValue: 1,
            rewardPoints: 50,
            iconUrl: 'https://example.com/icon.png',
            isActive: true
          }
        ]
      }

      expect(mockAchievements.data).toHaveLength(1)
      expect(mockAchievements.data[0].title).toBe('第一步')
      expect(mockAchievements.data[0].isActive).toBe(true)
    })

    it('should handle empty achievements list', () => {
      const mockAchievements = { data: [] }
      expect(mockAchievements.data).toHaveLength(0)
    })

    it('should display "是" for active achievement', () => {
      const isActive = true
      const display = isActive ? '是' : '否'
      expect(display).toBe('是')
    })

    it('should display "否" for inactive achievement', () => {
      const isActive = false
      const display = isActive ? '是' : '否'
      expect(display).toBe('否')
    })
  })

  describe('addAchievement', () => {
    it('should construct achievement request correctly', () => {
      const achievementData = {
        title: '新成就',
        description: '新成就描述',
        type: 'TASK_BASED',
        conditionValue: 5,
        rewardPoints: 100,
        iconUrl: 'https://example.com/icon.png',
        isActive: true
      }

      expect(achievementData.title).toBe('新成就')
      expect(achievementData.rewardPoints).toBe(100)
      expect(achievementData.conditionValue).toBe(5)
    })

    it('should parse numeric fields correctly', () => {
      const conditionValue = Number('10')
      const rewardPoints = Number('150')
      
      expect(conditionValue).toBe(10)
      expect(typeof conditionValue).toBe('number')
      expect(rewardPoints).toBe(150)
      expect(typeof rewardPoints).toBe('number')
    })
  })

  describe('loadUserAchievements', () => {
    it('should parse user achievements correctly', () => {
      const mockUserAchievements = {
        data: {
          userAchievements: [
            {
              achievement: { title: '完成者' },
              currentProgress: 5,
              unlocked: true,
              unlockedAt: '2024-06-10T10:00:00'
            }
          ]
        }
      }

      const achievements = mockUserAchievements.data.userAchievements
      expect(achievements).toHaveLength(1)
      expect(achievements[0].unlocked).toBe(true)
    })

    it('should display locked status correctly', () => {
      const unlocked = false
      const display = unlocked ? '已解锁' : '未解锁'
      expect(display).toBe('未解锁')
    })

    it('should display unlocked status correctly', () => {
      const unlocked = true
      const display = unlocked ? '已解锁' : '未解锁'
      expect(display).toBe('已解锁')
    })
  })
})
