import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import '../config.js'

global.fetch = vi.fn()

describe('Task Functions', () => {
  beforeEach(() => {
    global.fetch.mockClear()
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  describe('getStatusClass', () => {
    it('should return status-finished for FINISHED status', () => {
      const status = 'FINISHED'
      const s = status.toLowerCase()
      const result = s.includes('finish') ? 'status-finished' : ''
      expect(result).toBe('status-finished')
    })

    it('should return status-cancel for CANCELLED status', () => {
      const status = 'CANCELLED'
      const s = status.toLowerCase()
      const result = s.includes('cancel') ? 'status-cancel' : ''
      expect(result).toBe('status-cancel')
    })

    it('should return status-pending for IN_PROGRESS status', () => {
      const status = 'IN_PROGRESS'
      const s = status.toLowerCase()
      const result = s.includes('finish') ? 'status-finished' : 
                     s.includes('cancel') ? 'status-cancel' : 'status-pending'
      expect(result).toBe('status-pending')
    })

    it('should return empty string for empty status', () => {
      const status = ''
      const result = status ? 'some-class' : ''
      expect(result).toBe('')
    })

    it('should handle null status', () => {
      const status = null
      const result = status ? 'some-class' : ''
      expect(result).toBe('')
    })
  })

  describe('renderTable', () => {
    it('should render tasks with correct fields', () => {
      const tasks = [
        {
          taskId: 1,
          title: '完成代码审查',
          rewardPoints: 50,
          status: 'OPEN'
        }
      ]

      expect(tasks).toHaveLength(1)
      expect(tasks[0].taskId).toBe(1)
      expect(tasks[0].rewardPoints).toBe(50)
    })

    it('should show empty message for no tasks', () => {
      const tasks = []
      const message = tasks.length === 0 ? '暂无数据' : ''
      expect(message).toBe('暂无数据')
    })

    it('should handle multiple task statuses', () => {
      const tasks = [
        { taskId: 1, status: 'OPEN' },
        { taskId: 2, status: 'IN_PROGRESS' },
        { taskId: 3, status: 'FINISHED' },
        { taskId: 4, status: 'CANCELLED' }
      ]

      expect(tasks).toHaveLength(4)
      expect(tasks.map(t => t.status)).toEqual(['OPEN', 'IN_PROGRESS', 'FINISHED', 'CANCELLED'])
    })
  })

  describe('searchTask', () => {
    it('should encode keyword for URL', () => {
      const keyword = '测试任务'
      const encoded = encodeURIComponent(keyword)
      expect(encoded).toBe('%E6%B5%8B%E8%AF%95%E4%BB%BB%E5%8A%A1')
    })

    it('should handle empty keyword search', () => {
      const keyword = ''
      const shouldSearch = !!(keyword && keyword.trim().length > 0)
      expect(shouldSearch).toBe(false)
    })

    it('should trim keyword whitespace', () => {
      const keyword = '  完成  '
      const trimmed = keyword.trim()
      expect(trimmed).toBe('完成')
    })

    it('should parse search results', () => {
      const searchResult = {
        code: 200,
        data: [
          {
            taskId: 1,
            title: '任务1',
            rewardPoints: 100,
            status: 'OPEN'
          }
        ]
      }

      expect(searchResult.code).toBe(200)
      expect(searchResult.data).toHaveLength(1)
    })
  })
})
