"use client"

import React from "react"
import { Bell, Menu, QrCode, X, Home, Calendar, Music, BookOpen, Users, Phone } from "lucide-react"
import { Button } from "./ui/button"
import { useState } from "react"
import { cn } from "../lib/utils"

interface HeaderProps {
  activeTab?: string
  onTabChange?: (tab: string) => void
}

export function Header({ activeTab, onTabChange }: HeaderProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const menuItems = [
    { icon: Home, label: "홈", id: "home" },
    { icon: Calendar, label: "일정", id: "calendar" },
    { icon: Music, label: "악보", id: "music" },
    { icon: BookOpen, label: "말씀", id: "sermon" },
    { icon: Users, label: "소식", id: "news" },
    { icon: Phone, label: "연락처", id: "contact" },
  ]

  const handleMenuClick = (tabId: string) => {
    if (onTabChange) {
      onTabChange(tabId)
    }
    setIsMenuOpen(false)
  }

  return (
    <header className="sticky top-0 z-50 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 border-b border-border">
      <div className="container flex h-16 items-center justify-between px-4">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
            <span className="text-primary-foreground font-bold text-sm">교</span>
          </div>
          <div>
            <h1 className="font-bold text-lg text-foreground">새생명교회</h1>
            <p className="text-xs text-muted-foreground">주보 2024.03.19</p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button variant="ghost" size="icon">
            <QrCode className="h-5 w-5" />
          </Button>
          <Button variant="ghost" size="icon">
            <Bell className="h-5 w-5" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            {isMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </Button>
        </div>
      </div>

      {isMenuOpen && (
        <div className="absolute top-16 left-0 right-0 bg-card border-b border-border shadow-lg animate-in slide-in-from-top-2">
          <div className="container px-4 py-4">
            <nav className="grid grid-cols-2 gap-2">
              {menuItems.map(({ icon: Icon, label, id }) => (
                <button
                  key={id}
                  onClick={() => handleMenuClick(id)}
                  className={cn(
                    "flex items-center gap-3 px-4 py-3 rounded-lg transition-colors text-left",
                    activeTab === id
                      ? "bg-primary text-primary-foreground"
                      : "bg-muted hover:bg-muted/80 text-foreground",
                  )}
                >
                  <Icon className="h-5 w-5" />
                  <span className="font-medium">{label}</span>
                </button>
              ))}
            </nav>
          </div>
        </div>
      )}
    </header>
  )
}